import React, { useState } from 'react'

import "./Login.css"

const Login = ({ setUserId }) => {

  const [userName, setUserName] = useState("");
  const [userPass, setUserPass] = useState("");

  const [error, setError] = useState("");

  const LoginURL = "http://localhost:8080/login";

  const loginPost = () => {
    const login = async () => await fetch(LoginURL, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        username: userName,
        password: userPass
      })
    }).then((res) => res.json()
    ).then((json) => {
      console.log(json);
      if (json.error) {
        setError(json.error);
        return "";
      } else {
        setError("");
        //  使用できるように渡す
        setUserId(json.user_id);
        //  保存
        localStorage.setItem("userId", json.user_id);
        ;
      }
    }).catch((error) =>
      console.log(error)
    );
    login();
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