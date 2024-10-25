package routers

import (
	"simple-sns/controllers"

	"github.com/gin-contrib/cors"
	"github.com/gin-gonic/gin"
)

func Initialize() *gin.Engine {
	r := gin.Default()

	//	CROSエラーの対策
	r.Use(cors.New(cors.Config{
		AllowOrigins:     []string{"http://localhost:3000"}, // ReactアプリのURL
		AllowMethods:     []string{"GET", "POST", "PUT", "DELETE", "OPTIONS"},
		AllowHeaders:     []string{"Origin", "Content-Type", "Authorization"},
		ExposeHeaders:    []string{"Content-Length"},
		AllowCredentials: true,
		MaxAge:           12 * 60 * 60, // 12時間
	}))

	//	ユーザー関係
	//	登録 : {"username":"aaa","password":"aaa"} : {"user_id":1}
	r.POST("/signup", controllers.SignupHandler)
	//	ログイン : {"username":"aaa","password":"aaa"} : {"user_id":1}
	r.POST("/login", controllers.LoginHandler)
	//	削除 : {"user_id:"1"}
	r.DELETE("/user", controllers.DeleteUserHandler)

	//	投稿関係
	//	投稿
	r.POST("/post", controllers.CreatePostHandler)
	//	取得
	r.GET("/post", controllers.GetPostHandler)
	//	削除
	r.DELETE("/post", controllers.DeletePostHandler)

	return r
}
