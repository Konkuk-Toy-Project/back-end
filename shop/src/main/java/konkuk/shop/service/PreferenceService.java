package konkuk.shop.service;

import konkuk.shop.dto.PreferenceDto;
import konkuk.shop.entity.Item;
import konkuk.shop.entity.Member;
import konkuk.shop.entity.PreferenceItem;
import konkuk.shop.error.ApiException;
import konkuk.shop.error.ExceptionEnum;
import konkuk.shop.repository.ItemRepository;
import konkuk.shop.repository.MemberRepository;
import konkuk.shop.repository.PreferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PreferenceService {
    private final PreferenceRepository preferenceRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    public Long savePreferenceItem(Long memberId, Long itemId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.NO_FIND_MEMBER));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.NO_FIND_ITEM_BY_ID));
        PreferenceItem savePreferenceItem = preferenceRepository.save(new PreferenceItem(member, item));
        member.getPreferenceItems().add(savePreferenceItem);

        item.plusPreferenceCount();
        itemRepository.save(item);
        return savePreferenceItem.getId();
    }

    public List<PreferenceDto> findPreferenceByMemberId(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.NO_FIND_MEMBER));

        List<PreferenceDto> result = member.getPreferenceItems()
                .stream().map(e -> {
                    Item item = e.getItem();
                    return new PreferenceDto(item.getThumbnail().getStore_name(),
                            item.getName(), item.getPrice(), item.getSale(), e.getId());
                })
                .collect(Collectors.toList());
        return result;
    }

    @Transactional
    public void deletePreference(Long memberId, Long preferenceId) {
        PreferenceItem preferenceItem = preferenceRepository.findById(preferenceId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.NO_FIND_PREFERENCE));

        if (preferenceItem.getMember().getId() != memberId)
            throw new ApiException(ExceptionEnum.NOT_AUTHORITY_PREFERENCE_EDIT);
        preferenceItem.getItem().minusPreferenceCount();

        preferenceRepository.delete(preferenceItem);
    }
}
