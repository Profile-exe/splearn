package tobyspring.splearn.domain.member;

public class MemberFixture {
    public static MemberRegisterRequest createMemberRegisterRequest(String email) {
        return new MemberRegisterRequest(email, "DDING", "verysecret");
    }

    public static MemberRegisterRequest createMemberRegisterRequest() {
        return createMemberRegisterRequest("test@splearn.app");
    }

    public static MemberInfoUpdateRequest createMemberInfoUpdateRequest(String profileAddress) {
        return new MemberInfoUpdateRequest("Peter", profileAddress, "자기소개");
    }

    public static MemberInfoUpdateRequest createMemberInfoUpdateRequest() {
        return createMemberInfoUpdateRequest("peter123");
    }

    public static PasswordEncoder createPasswordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(String password) {
                return password.toUpperCase();
            }

            @Override
            public boolean matches(String password, String passwordHash) {
                return encode(password).equals(passwordHash);
            }
        };
    }
}
