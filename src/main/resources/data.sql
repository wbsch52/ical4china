CREATE TABLE IF NOT EXISTS subscription_records (
                              id INT AUTO_INCREMENT  PRIMARY KEY,
                              ip VARCHAR(128) NOT NULL,
                              times INT(11) UNSIGNED NOT NULL DEFAULT 1,
                              created_date INT(11) UNSIGNED NOT NULL,
                              modified_date INT(11) UNSIGNED DEFAULT NULL,
                              UNIQUE INDEX `unq_ip` (ip)
);