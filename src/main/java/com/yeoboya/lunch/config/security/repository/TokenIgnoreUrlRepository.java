package com.yeoboya.lunch.config.security.repository;

import com.yeoboya.lunch.api.v1.common.response.Response;
import com.yeoboya.lunch.config.security.domain.TokenIgnoreUrl;
import com.yeoboya.lunch.config.security.reqeust.TokenIgnoreUrlRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TokenIgnoreUrlRepository {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<TokenIgnoreUrl> mapper = (rs, rowNum) -> {
        TokenIgnoreUrl tokenIgnoreUrl = new TokenIgnoreUrl();
        tokenIgnoreUrl.setId(rs.getLong("token_ignore_id"));
        tokenIgnoreUrl.setUrl(rs.getString("url"));
        tokenIgnoreUrl.setIsIgnore(rs.getBoolean("is_ignore"));
        return tokenIgnoreUrl;
    };

    public List<TokenIgnoreUrl> getTokenIgnoreUrls() {
        return jdbcTemplate.query("SELECT token_ignore_id, url, is_ignore FROM token_ignore_urls ORDER BY token_ignore_id desc ", mapper);
    }

    public Optional<TokenIgnoreUrl> findTokenIgnoreUrlByUrl(String url) {
        String sql = "SELECT * FROM token_ignore_urls WHERE url = ?";
        List<TokenIgnoreUrl> results = jdbcTemplate.query(sql,
                (rs, rowNum) -> {
                    TokenIgnoreUrl tokenIgnoreUrl = new TokenIgnoreUrl();
                    tokenIgnoreUrl.setId(rs.getLong("token_ignore_id"));
                    tokenIgnoreUrl.setUrl(rs.getString("url"));
                    tokenIgnoreUrl.setIsIgnore(rs.getBoolean("is_ignore"));
                    return tokenIgnoreUrl;
                },  url);

        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public int insertOrUpdateTokenIgnoreUrl(TokenIgnoreUrlRequest tokenIgnoreUrlRequest) {
        Optional<TokenIgnoreUrl> result = this.findTokenIgnoreUrlByUrl(tokenIgnoreUrlRequest.getUrl());

        if (result.isPresent()) {
            // ✅ 존재하면 UPDATE 수행
            String updateSql = "UPDATE token_ignore_urls SET is_ignore = ? WHERE url = ?";
            return jdbcTemplate.update(updateSql, tokenIgnoreUrlRequest.isIgnore(), tokenIgnoreUrlRequest.getUrl());
        } else {
            try {
                // ✅ 존재하지 않으면 INSERT 수행
                String insertSql = "INSERT INTO token_ignore_urls(url, is_ignore) VALUES (?, ?)";
                return jdbcTemplate.update(insertSql, tokenIgnoreUrlRequest.getUrl(), tokenIgnoreUrlRequest.isIgnore());
            } catch (DuplicateKeyException e) {
                // ✅ 동시성 이슈로 인해 INSERT 시 중복 발생할 경우 UPDATE 수행
                String updateSql = "UPDATE token_ignore_urls SET is_ignore = ? WHERE url = ?";
                return jdbcTemplate.update(updateSql, tokenIgnoreUrlRequest.isIgnore(), tokenIgnoreUrlRequest.getUrl());
            }
        }
    }


    // ✅ 토큰 무시 URL 삭제
    public int deleteTokenIgnoreUrl(Long id) {
        String deleteSql = "DELETE FROM token_ignore_urls WHERE token_ignore_id = ?";
        return jdbcTemplate.update(deleteSql, id); // 삭제된 행 수 반환
    }

}
