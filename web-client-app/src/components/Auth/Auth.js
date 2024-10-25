import React from 'react'
import Logout from './Logout';
import SignUp from './Sinup';
import Login from './Login';

const Auth = ({ userId, setUserId,}) => {
  return (
    <div>
      {userId && userId !== "" ?
        (
          <>
            {/* ログインしていたら */}
            <Logout setUserId={setUserId} />

          </>
        )
        :
        (
          <>
            {/* // ログインしていなかったら */}
            <h2>新規登録</h2>
            <SignUp setUserId={setUserId} />
            <h2>ログイン</h2>
            <Login setUserId={setUserId} />
          </>
        )
      }
    </div>
  )
}

export default Auth