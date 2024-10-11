package controllers

import (
	"fmt"
	"github.com/gin-gonic/gin"
	"golang.org/x/crypto/bcrypt"
	"gorm.io/gorm"
	"net/http"
	"simple-sns/database"
	"simple-sns/models"
	"strconv"
	"strings"
)

func SignupHandler(c *gin.Context) {
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

	id, err := createUser(database.DB, input.Username, input.Password)

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

func LoginHandler(c *gin.Context) {
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
	user, result := getUserFromUserName(database.DB, input.Username)
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

func DeleteUserHandler(c *gin.Context) {
	var input struct {
		UserID string `json:"user_id" binding:"required"`
	}

	if err := c.ShouldBindBodyWithJSON(&input); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "入力データが正しくありません"})
	}

	//	stringをintに変換
	userID, err := strconv.Atoi(input.UserID)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "user_idは整数である必要がります。"})
	}

	// ユーザーの削除
	result := database.DB.Delete(&models.User{}, userID)
	if result.Error != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "ユーザーの削除に失敗しました"})
		return
	}

	if result.RowsAffected == 0 {
		c.JSON(http.StatusNotFound, gin.H{"error": "指定されたユーザーは存在しません"})
		return
	}

	c.Status(http.StatusOK)
}

func getUserFromUserName(db *gorm.DB, username string) (models.User, bool) {
	var user models.User
	if err := db.Where("username = ?", username).First(&user).Error; err != nil {
		return user, false
	}
	return user, true
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
