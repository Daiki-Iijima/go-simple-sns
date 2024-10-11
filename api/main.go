package main

import (
	"fmt"
	"github.com/gin-gonic/gin"
	"golang.org/x/crypto/bcrypt"
	"gorm.io/driver/postgres"
	"gorm.io/gorm"
	"log"
	"net/http"
	"simple-sns/models"
	"strings"
)

func initializeDB() (*gorm.DB, error) {
	dsn := "host=simple-sns-db-1 user=user password=pass dbname=simple-sns port=5432 sslmode=disable TimeZone=Asia/Tokyo"
	db, err := gorm.Open(postgres.Open(dsn), &gorm.Config{})
	if err != nil {
		log.Fatal(err)
	}
	fmt.Println("DBへの接続成功")

	return db, err
}

func createUser(db *gorm.DB, username string, password string) (int, error) {
	// パスワードのハッシュ化
	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(password), bcrypt.DefaultCost)
	if err != nil {
		return -1, err
	}

	user := models.User{
		Username:     username,
		PasswordHash: string(hashedPassword),
	}
	//	自動生成される部分もあるので参照を渡して、データを埋めてもらう
	result := db.Create(&user)
	if result.Error != nil {
		return -1, result.Error
	}

	fmt.Printf("新しいユーザーが作成されました。ID:%d\n", user.ID)

	return int(user.ID), nil
}

func getUserFromUserName(db *gorm.DB, username string) (models.User, bool) {
	var user models.User
	if err := db.Where("username = ?", username).First(&user).Error; err != nil {
		return user, false
	}
	return user, true
}

var db *gorm.DB
var err error

func main() {
	r := gin.Default()

	r.GET("/", func(c *gin.Context) {
		c.String(http.StatusOK, "Hello,World!")
	})

	r.DELETE("/users", func(c *gin.Context) {
		db.Exec("DELETE FROM users")
		c.String(http.StatusOK, "Success")
	})

	r.POST("/signup", signupHandler)

	r.POST("/signin", signinHandler)

	//	DBへの接続
	db, err = initializeDB()

	if err != nil {
		log.Fatal(err)
	}

	//	サーバーの起動
	err := r.Run(":8080")
	if err != nil {
		log.Fatal(err)
	}
}

func signupHandler(c *gin.Context) {
	// リクエストボディのデータを入れる構造体を定義
	var input struct {
		Username string `json:"username" binding:"required"`
		Password string `json:"password" binding:"required"`
	}

	//	送られてきたJSONデータを作成した構造体にバインド
	if err := c.ShouldBindBodyWithJSON(&input); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "入力データが正しくありません"})
		return
	}

	//	ユーザー名とパスワードのバリデーション
	if len(input.Username) == 0 || len(input.Password) == 0 {
		c.JSON(http.StatusBadRequest, gin.H{"error": "ユーザー名とパスワードは必須です"})
		return
	}

	id, err := createUser(db, input.Username, input.Password)

	if err != nil {
		//	エラーの詳細を返す
		if strings.Contains(err.Error(), "duplicate key value") || strings.Contains(err.Error(), "UNIQUE constraint failed") {
			c.JSON(http.StatusConflict, gin.H{"error": "ユーザー名はすでに存在します"})
		} else {
			c.JSON(http.StatusInternalServerError, gin.H{"error": "ユーザーの作成に失敗しました"})
		}

		return
	}

	c.JSON(http.StatusOK, gin.H{"user_id": id})
}

func signinHandler(c *gin.Context) {
	// リクエストボディのデータを入れる構造体を定義
	var input struct {
		Username string `json:"username" binding:"required"`
		Password string `json:"password" binding:"required"`
	}

	//	送られてきたJSONデータを作成した構造体にバインド
	if err := c.ShouldBindBodyWithJSON(&input); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "入力データが正しくありません"})
		return
	}

	//	ユーザー名とパスワードのバリデーション
	if len(input.Username) == 0 || len(input.Password) == 0 {
		c.JSON(http.StatusBadRequest, gin.H{"error": "ユーザー名とパスワードは必須です"})
		return
	}

	//	ユーザー名からユーザーを取得
	user, result := getUserFromUserName(db, input.Username)
	if result == false {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "ユーザー名が間違っています"})
		return
	}

	// パスワードの検証
	if err := bcrypt.CompareHashAndPassword([]byte(user.PasswordHash), []byte(input.Password)); err != nil {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "パスワードが間違っています"})
		return
	}

	//	成功したのでとりあえずIDを返す
	c.JSON(http.StatusOK, gin.H{"user_id": user.ID})
}
