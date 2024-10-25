import React from 'react'

import { useNavigate } from 'react-router-dom';

import "./Home.css"

const Home = ({ userId, json, setSendContent, sendPost }) => {

  const navigator = useNavigate();

  console.log(userId);

  return (
    <div className='home'>
      {/* 投稿入力欄 */}
      <div className='header'>
        <div className='userId'>
          <p>ユーザーID <br />{userId}</p>
        </div>
        <div className='inputField'>
          <input
            type='text'
            placeholder='10文字以内で入力してや'
            onChange={(value) => {
              setSendContent(value.target.value);
            }}
          />
          <button onClick={sendPost} >
            送信
          </button>
        </div>
        <button className='authBtn' onClick={() => {
          navigator("/auth");
        }
        }>{userId === null || userId === undefined ? "ログイン" : "ログアウト"}</button>
      </div>
      {/* 投稿一覧 */}
      {json && json.map((post) =>
        <div className='postCard' key={post.id}>
          <div className='postHeader'>
            <p className='postId'>{post.user_id}</p>
            <p className='postTime'>{
              new Date(post.created_at).toLocaleString('ja-JP', { timeZone: 'Asia/Tokyo' })}
            </p>
          </div>
          <h3 className='postContent'>{post.content}</h3>
        </div>
      )
      }
    </div>
  )
}

export default Home