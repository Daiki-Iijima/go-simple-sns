package main

import (
	"log"
	"simple-sns/database"
	"simple-sns/routers"
)

func main() {

	//	DBへの接続
	err := database.InitializeDB()
	if err != nil {
		log.Fatal(err)
	}

	//	サーバーの生成
	router := routers.Initialize()

	//	サーバーの起動
	err = router.Run(":8080")
	if err != nil {
		log.Fatal(err)
	}
}
