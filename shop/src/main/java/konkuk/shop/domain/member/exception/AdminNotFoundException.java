package konkuk.shop.domain.member.exception;


import konkuk.shop.global.exception.ErrorCode;
import konkuk.shop.global.exception.NotFoundException;

public class AdminNotFoundException extends NotFoundException {

    public AdminNotFoundException() {
        super(ErrorCode.NOT_ADMIN_MEMBER);
    }
}
