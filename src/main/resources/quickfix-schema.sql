-- QuickFIX/J JDBC Store Schema (H2 compatible)

CREATE TABLE IF NOT EXISTS sessions (
    beginstring CHAR(8) NOT NULL,
    sendercompid VARCHAR(64) NOT NULL,
    targetcompid VARCHAR(64) NOT NULL,
    session_qualifier VARCHAR(64) NOT NULL,
    creation_time TIMESTAMP NOT NULL,
    incoming_seqnum INTEGER NOT NULL,
    outgoing_seqnum INTEGER NOT NULL,
    PRIMARY KEY (beginstring, sendercompid, targetcompid, session_qualifier)
);

CREATE TABLE IF NOT EXISTS messages (
    beginstring CHAR(8) NOT NULL,
    sendercompid VARCHAR(64) NOT NULL,
    targetcompid VARCHAR(64) NOT NULL,
    session_qualifier VARCHAR(64) NOT NULL,
    msgseqnum INTEGER NOT NULL,
    message TEXT NOT NULL,
    PRIMARY KEY (beginstring, sendercompid, targetcompid, session_qualifier, msgseqnum)
);

CREATE TABLE IF NOT EXISTS outgoing_messages (
    beginstring CHAR(8) NOT NULL,
    sendercompid VARCHAR(64) NOT NULL,
    targetcompid VARCHAR(64) NOT NULL,
    session_qualifier VARCHAR(64) NOT NULL,
    msgseqnum INTEGER NOT NULL,
    message TEXT NOT NULL,
    PRIMARY KEY (beginstring, sendercompid, targetcompid, session_qualifier, msgseqnum)
);

CREATE TABLE IF NOT EXISTS logs (
    time TIMESTAMP NOT NULL,
    beginstring CHAR(8),
    sendercompid VARCHAR(64),
    targetcompid VARCHAR(64),
    session_qualifier VARCHAR(64),
    text TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS event_logs (
    time TIMESTAMP NOT NULL,
    beginstring CHAR(8),
    sendercompid VARCHAR(64),
    targetcompid VARCHAR(64),
    session_qualifier VARCHAR(64),
    text TEXT NOT NULL
);
