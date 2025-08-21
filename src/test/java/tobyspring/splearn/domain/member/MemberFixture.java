package tobyspring.splearn.domain.member;

public class MemberFixture {
    public static MemberRegisterRequest createMemberRegisterRequest() {
        return new MemberRegisterRequest("test@splearn.app", "DDING", "verysecret");
    }

    public static MemberInfoUpdateRequest createMemberInfoUpdateRequest() {
        return new MemberInfoUpdateRequest("Peter", "peter123", "자기소개");
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
