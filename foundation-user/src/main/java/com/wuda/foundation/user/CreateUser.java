package com.wuda.foundation.user;

import lombok.Getter;

import java.util.Objects;

/**
 * for create user.
 *
 * @author wuda
 * @since 1.0.0
 */
@Getter
public class CreateUser {

    private UserType userType;
    private UserState userState;
    private CreateUserAccount userAccount;

    /**
     * 禁止实例化,使用{@link Builder}实例化.
     */
    private CreateUser() {

    }

    /**
     * 用于创建{@link CreateUser}.
     *
     * @author wuda
     * @since 1.0.0
     */
    public static class Builder implements com.wuda.foundation.lang.Builder<CreateUser> {

        private UserType userType;
        private UserState userState;
        private CreateUserAccount userAccount;

        public Builder setUserType(UserType userType) {
            this.userType = userType;
            return this;
        }

        public Builder setUserState(UserState userState) {
            this.userState = userState;
            return this;
        }

        public Builder setUserAccount(CreateUserAccount userAccount) {
            this.userAccount = userAccount;
            return this;
        }

        @Override
        public CreateUser build() {
            CreateUser createUser = new CreateUser();
            createUser.userType = Objects.requireNonNull(this.userType);
            createUser.userState = Objects.requireNonNull(this.userState);
            createUser.userAccount = Objects.requireNonNull(this.userAccount);
            return createUser;
        }
    }
}
