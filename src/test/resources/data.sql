INSERT INTO sms_request (id, msisdn, originator, message, create_date)
	VALUES (1, '1555424238', 'INBOX', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.', SYSDATE);
INSERT INTO sms_chunk (id, id_request, msisdn, originator, udh, message, last, retries)
	VALUES (1, 1, '1555424238', 'INBOX', '050003AB0401', '0042FF2388120023130042FF2388120023130042FF2388120023130042FF2388120023130042FF2388120023130042FF238812002313', false, 1);
INSERT INTO sms_chunk (id, id_request, msisdn, originator, udh, message, last, retries)
	VALUES (2, 1, '1555424238', 'INBOX', '050003AB0402', '459AFF235BEE09459AFF235BEE09459AFF235BEE09459AFF235BEE09459AFF235BEE09459AFF235BEE09459AFF235BEE09459AFF235BEE09', false, 0);
INSERT INTO sms_chunk (id, id_request, msisdn, originator, udh, message, last, retries)
	VALUES (3, 1, '1555424238', 'INBOX', '050003AB0403', '9459AFF235BEE09459AFFF23881200239459AFF235BEE09459AFFF23881200239459AFF235BEE09459AFFF23881200239459AFF235', false, 0);
INSERT INTO sms_chunk (id, id_request, msisdn, originator, udh, message, last, retries)
	VALUES (4, 1, '1555424238', 'INBOX', '050003AB0404', '5BEE09459AFFF23881200239459AFF', true, 0);

INSERT INTO sms_request (id, msisdn, originator, message, create_date)
	VALUES (2, '1555424238', 'dpradom', 'short test message.', SYSDATE);
INSERT INTO sms_chunk (id, id_request, msisdn, originator, udh, message, last, retries)
	VALUES (5, 2, '1555424238', 'dpradom', null, 'short test message.', true, 0);

	
