CREATE TABLE admin (
    id VARCHAR(255) PRIMARY KEY,
    admin_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR (15) NOT NULL,
    password VARCHAR(255) NOT NULL,
    address VARCHAR (255) NOT NULL,
    admin_profile_pic VARCHAR (255) NOT NULL,
    otp INT (10),
    uuid VARCHAR(200)
);