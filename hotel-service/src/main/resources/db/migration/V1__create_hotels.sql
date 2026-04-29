CREATE TABLE hotel (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    address VARCHAR(255),
    city VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,
    star_rating INTEGER,
    phone VARCHAR(30),
    email VARCHAR(100),
    description TEXT
);

CREATE TABLE amenity (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE room (
    id BIGSERIAL PRIMARY KEY,
    room_number INTEGER NOT NULL UNIQUE,
    type VARCHAR(20) NOT NULL,
    comfort VARCHAR(20) NOT NULL,
    price_per_night DOUBLE PRECISION NOT NULL,
    capacity INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    description TEXT,
    hotel_id BIGINT REFERENCES hotel(id) ON DELETE CASCADE
);

CREATE TABLE room_amenity (
    room_id BIGINT NOT NULL REFERENCES room(id) ON DELETE CASCADE,
    amenity_id BIGINT NOT NULL REFERENCES amenity(id) ON DELETE CASCADE,
    PRIMARY KEY (room_id, amenity_id)
);

CREATE TABLE review (
    id BIGSERIAL PRIMARY KEY,
    room_id BIGINT NOT NULL REFERENCES room(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL,
    username VARCHAR(50) NOT NULL,
    rating INTEGER NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
