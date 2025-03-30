CREATE TABLE device
(
    mac_address       VARCHAR(255) NOT NULL,
    model             VARCHAR(255) NOT NULL,
    override_fragment VARCHAR(255),
    username          VARCHAR(255),
    password          VARCHAR(255),
    CONSTRAINT pk_device PRIMARY KEY (mac_address)
);