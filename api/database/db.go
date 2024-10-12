package database

import (
	"gorm.io/driver/postgres"
	"gorm.io/gorm"
)

var DB *gorm.DB

func InitializeDB() error {
	dsn := "host=go-simple-sns-db-1 user=user password=pass dbname=simple-sns port=5432 sslmode=disable TimeZone=Asia/Tokyo"
	db, err := gorm.Open(postgres.Open(dsn), &gorm.Config{})

	//	エラーがなかったらデータベースの参照を保存
	if err == nil {
		DB = db
	}

	return err
}
