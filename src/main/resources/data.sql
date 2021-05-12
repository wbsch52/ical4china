CREATE TABLE IF NOT EXISTS subscription_records (
                              id INT AUTO_INCREMENT  PRIMARY KEY,
                              ip VARCHAR(128) NOT NULL,
                              created_date int(11) unsigned NOT NULL,
                              modified_date int(11) unsigned DEFAULT NULL,
                              unique index `unq_ip` (ip)
);