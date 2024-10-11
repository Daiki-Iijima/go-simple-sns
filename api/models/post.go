package models

import "time"

type Post struct {
	ID        uint      `gorm:"primaryKey" json:"id"`
	Content   string    `gorm:"size:10;not null" json:"content"`
	UserID    uint      `gorm:"not null" json:"user_id"`
	User      User      `gorm:"foreignKey:UserID" json:"user"`
	CreatedAt time.Time `json:"created_at"`
	UpdatedAt time.Time `json:"updated_at"`
}
