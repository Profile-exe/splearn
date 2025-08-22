package tobyspring.splearn.domain.member;

import static java.util.Objects.requireNonNull;
import static org.springframework.util.Assert.isTrue;

import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import tobyspring.splearn.domain.AbstractEntity;

@Entity
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberDetail extends AbstractEntity {
    private Profile profile;

    private String introduction;

    private LocalDateTime registeredAt;

    private LocalDateTime activatedAt;

    private LocalDateTime deactivatedAt;

    static MemberDetail create() {
        MemberDetail memberDetail = new MemberDetail();
        memberDetail.registeredAt = LocalDateTime.now();
        return memberDetail;
    }

    void activate() {
        isTrue(activatedAt == null, "이미 activatedAt은 설정되었습니다");

        this.activatedAt = LocalDateTime.now();
    }

    void deactivate() {
        isTrue(deactivatedAt == null, "이미 deactivatedAt은 설정되었습니다");

        this.deactivatedAt = LocalDateTime.now();
    }

    void updateInfo(MemberInfoUpdateRequest updateRequest) {
        this.profile = new Profile(updateRequest.profileAddress());
        this.introduction = requireNonNull(updateRequest.introduction());
    }
}
