package cotato.csquiz.config.jwt;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@RedisHash(value = "jwtToken", timeToLive = 60 * 60 * 24 * 3)
public class RefreshToken {

    @Id
    private Long id;

    private String refreshToken;

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
