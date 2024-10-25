import React from 'react'

const Logout = ({ setUserId }) => {
  return (
    <button onClick={() => {
      localStorage.clear();
      setUserId("");
    }
    }>ログアウト</button>
  )
}

export default Logout