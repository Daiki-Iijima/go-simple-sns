package routers

import (
	"github.com/gin-gonic/gin"
	"simple-sns/controllers"
)

func Initialize() *gin.Engine {
	r := gin.Default()

	//	ユーザー関係
	//	登録 : {"username":"aaa","password":"aaa"} : {"user_id":1}
	r.POST("/signup", controllers.SignupHandler)
	//	ログイン : {"username":"aaa","password":"aaa"} : {"user_id":1}
	r.POST("/login", controllers.LoginHandler)
	//	削除 : {"user_id:"1"}
	r.DELETE("/user", controllers.DeleteUserHandler)

	return r
}
