package controllers

import (
	"github.com/gin-gonic/gin"
	"net/http"
	"simple-sns/database"
	"simple-sns/models"
	"strconv"
)

func CreatePostHandler(c *gin.Context) {

	//	リクエストボディからデータを取得
	var input struct {
		UserID  string `json:"user_id" binding:"required"`
		Content string `json:"content" binding:"required"`
	}

	//	送られてきたJSONデータを作成した構造体にバインド
	if err := c.ShouldBindBodyWithJSON(&input); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "入力データが正しくありません"})
		return
	}

	var uintUserID uint
	if tmpID, err := strconv.Atoi(input.UserID); err == nil {
		uintUserID = uint(tmpID)
	} else {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "存在しないユーザーです"})
		return
	}

	// UserIDが存在するかチェック
	var user models.User
	if err := database.DB.First(&user, uintUserID).Error; err != nil {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "存在しないユーザーです"})
		return
	}

	//	投稿内容のバリデーション
	if len([]rune(input.Content)) > 10 {
		c.JSON(http.StatusBadRequest, gin.H{"error": "投稿は10文字以内である必要があります"})
		return
	}

	//	投稿
	post := models.Post{
		Content: input.Content,
		UserID:  uintUserID,
	}

	result := database.DB.Create(&post)
	if result.Error != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "投稿の作成に失敗しました"})
		return
	}

	c.JSON(http.StatusOK, gin.H{"post_id": post.ID})
}

func GetPostHandler(c *gin.Context) {
	limit := 10
	if l := c.Query("limit"); l != "" {
		if parsedLimit, err := strconv.Atoi(l); err == nil && parsedLimit > 0 {
			limit = parsedLimit
		}
	}

	var posts []models.Post
	result := database.DB.Order("created_at desc").Limit(limit).Find(&posts)
	if result.Error != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "投稿の取得に失敗しました"})
		return
	}

	c.JSON(http.StatusOK, gin.H{"posts": posts})
}

func DeletePostHandler(c *gin.Context) {
	var input struct {
		UserID uint `json:"user_id" binding:"required"`
		PostID uint `json:"post_id" binding:"required"`
	}

	if err := c.ShouldBindBodyWithJSON(&input); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "入力データが正しくありません"})
		return
	}

	//  データベースから、userIdとpostIdが一致する投稿を取得
	var post models.Post
	if err := database.DB.
		Where("user_id = ? AND id = ?", input.UserID, input.PostID).
		First(&post).Error; err != nil {
		c.JSON(http.StatusNotFound, gin.H{"error": "指定された投稿は存在しません"})
		return
	}

	// 投稿の削除
	result := database.DB.Delete(&models.Post{}, input.PostID)
	if result.Error != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "投稿の削除に失敗しました"})
		return
	}

	if result.RowsAffected == 0 {
		c.JSON(http.StatusNotFound, gin.H{"error": "指定された投稿は存在しません"})
		return
	}

	//	削除成功したので200を返す
	c.Status(http.StatusOK)
}
