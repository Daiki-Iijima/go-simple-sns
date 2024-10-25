import React, { useState } from 'react'

import "./Login.css"
import { useNavigate } from 'react-router-dom';
import { SendPost } from '../../utils/RequestUtil';

const Login = ({ setUserId }) => {

  const [userName, setUserName] = useState("");
  const [userPass, setUserPass] = useState("");

  const [error, setError] = useState("");

  const LoginUrl = "http://localhost:8080/login";

  const navigate = useNavigate();

  const loginPost = async () => {
    const result = await SendPost(LoginUrl, {
      username: userName,
      password: userPass
    });

    // 実行結果を取得
    if (result.error) {
      setError(result.error);
    } else {
      setError("");
      //  使用できるように渡す
      setUserId(result.user_id);
      //  保存
      localStorage.setItem("userId", result.user_id);

      navigate("/");
    }
  }

  return (
    <div className='loginForm'>
      {/* エラーの表示 */}
      {error !== "" ? <p>{error}</p> : ""}
      <input
        type='text'
        placeholder='ユーザー名'
        onChange={(e) => {
          setUserName(e.target.value);
        }}
      />
      <input
        type='text'
        placeholder='パスワード'
        onChange={(e) => {
          setUserPass(e.target.value);
        }}
      />
      <button onClick={loginPost}>ログイン</button>
    </div>
  )
}

export default Login