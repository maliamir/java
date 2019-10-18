DROP TABLE IF EXISTS customers;

CREATE TABLE customers (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  first_name VARCHAR(250) NOT NULL,
  last_name VARCHAR(250) NOT NULL
);

/*
INSERT INTO customers (first_name, last_name) VALUES
  ('Aliko', 'Dangote'),
  ('Bill', 'Gates'),
  ('Larry', 'Page'),
  ('Mark', 'Hurd'),
  ('Folrunsho', 'Alakija');
*/