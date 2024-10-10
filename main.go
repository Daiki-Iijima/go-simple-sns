package main

import (
	"github.com/gin-gonic/gin"
	"log"
	"net/http"
)

func main() {
	r := gin.Default()

	r.GET("/", func(c *gin.Context) {
		c.String(http.StatusOK, "Hello,World!")
	})

	err := r.Run("localhost:8080")
	if err != nil {
		log.Fatal(err)
	}
}
