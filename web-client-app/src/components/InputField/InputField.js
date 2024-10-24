import React from 'react'

import "./InputField.css"

const InputField = ({ sendPost, setSendContent }) => {

  return (
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
  )
}

export default InputField