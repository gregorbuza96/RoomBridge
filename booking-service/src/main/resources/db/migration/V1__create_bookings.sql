CREATE TABLE booking (
    id BIGSERIAL PRIMARY KEY,
    room_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    total_price DOUBLE PRECISION,
    status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED'
);

CREATE INDEX idx_booking_room_id ON booking(room_id);
CREATE INDEX idx_booking_user_id ON booking(user_id);
CREATE INDEX idx_booking_dates ON booking(check_in_date, check_out_date);
