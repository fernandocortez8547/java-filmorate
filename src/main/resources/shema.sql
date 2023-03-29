CREATE TABLE IF NOT EXISTS film (
  film_id int GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  film_name varchar(40) NOT NULL,
  film_description varchar(200) NOT NULL,
  release_date date NOT NULL,
  duration int NOT NULL
);

CREATE TABLE IF NOT EXISTS "user" (
  user_id int GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  email varchar(200) NOT NULL,
  login varchar(40) NOT NULL,
  name varchar(40),
  birthday date NOT NULL
);

CREATE TABLE IF NOT EXISTS "friends_request" (
  "user_id" int REFERENCES "user"("user_id"),
  "friend_id" int REFERENCES "user"("user_id"),
  "status" boolean
);

CREATE TABLE IF NOT EXISTS genre (
  genre_id int PRIMARY KEY,
  name varchar(5)
);

CREATE TABLE IF NOT EXISTS film_genres (
  film_id int REFERENCES film(film_id),
  genre_id int REFERENCES genre(genre_id)
);

CREATE TABLE IF NOT EXISTS rating (
  rating_id int PRIMARY KEY,
  name varchar(20)
);

CREATE TABLE IF NOT EXISTS film_rating (
  film_id int REFERENCES film(film_id),
  rating_id int REFERENCES rating(rating_id)
);

CREATE TABLE IF NOT EXISTS film_like (
  film_id int REFERENCES film(film_id),
  user_id int REFERENCES "user"(user_id)
);