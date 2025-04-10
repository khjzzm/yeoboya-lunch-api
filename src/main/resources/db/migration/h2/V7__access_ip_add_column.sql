ALTER TABLE access_ip
    ADD expires_at TIMESTAMP;

ALTER TABLE access_ip
    ADD hit_count INT;

ALTER TABLE access_ip
    ADD reason VARCHAR(255);