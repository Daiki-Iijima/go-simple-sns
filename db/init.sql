-- データベースの作成
CREATE DATABASE simple_sns WITH ENCODING 'UTF8' LC_COLLATE 'C' LC_CTYPE 'C' TEMPLATE template0;

-- タイムゾーンの設定
SET TIMEZONE TO 'UTC';

-- ユーザーアカウントテーブルを作成
CREATE TABLE users(
  id SERIAL PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


-- 投稿テーブルを作成
CREATE TABLE posts(
  id SERIAL PRIMARY KEY,
  content VARCHAR(10) NOT NULL,
  user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- postsテーブルのuser_idにインデックスを追加
CREATE INDEX idx_posts_user_id ON posts(user_id);

-- postsテーブルのcreated_atにインデックスを追加
CREATE INDEX idx_posts_created_at ON posts(created_at DESC);