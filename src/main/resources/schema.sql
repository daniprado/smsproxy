CREATE TABLE sms_request (
  id IDENTITY PRIMARY KEY,
  msisdn VARCHAR(11) NOT NULL,
  originator VARCHAR(11) NOT NULL,
  message VARCHAR(4000) NOT NULL,
  create_date DATETIME NOT NULL,
  send_date DATETIME,
  messagebird_url VARCHAR(100)
);

CREATE TABLE sms_chunk (
  id IDENTITY PRIMARY KEY,
  id_request INT NOT NULL,
  msisdn VARCHAR(11) NOT NULL,
  originator VARCHAR(11) NOT NULL,
  udh VARCHAR(12),
  message VARCHAR(306) NOT NULL,
  last BOOLEAN NOT NULL,
  retries INT NOT NULL,
  CONSTRAINT fk_sms_request_shadow FOREIGN KEY(id_request) REFERENCES sms_request(id)
);
