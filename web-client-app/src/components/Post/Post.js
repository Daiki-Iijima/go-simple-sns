import React from 'react'

import "./Post.css"

const Post = ({ content, userId, createTime }) => {
  return (
    <div className='postCard'>
      <div className='postHeader'>
        <p className='postId'>{userId}</p>
        <p className='postTime'>{new Date(createTime).toLocaleString('ja-JP', { timeZone: 'Asia/Tokyo' })}</p>
      </div>
      <h3 className='postContent'>{content}</h3>
    </div>
  )
}

export default Post