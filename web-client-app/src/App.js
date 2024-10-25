import './App.css';
import { useEffect, useState } from "react"
import Post from './components/Post/Post';
import InputField from './components/InputField/InputField';

//  ルーティング
import { BrowserRouter as Router, Routes, Route } from "react-router-dom"
import Login from './components/Auth/Login';
import Logout from './components/Auth/Logout';
import SignUp from './components/Auth/Sinup';

function App() {

  const URL = "http://localhost:8080/post";
  const [json, setJson] = useState(null)

  const [userId, setUserId] = useState(localStorage.getItem("userId"));
  const [sendContent, setSendContent] = useState("");

  const SendPost = () => {
    fetch(URL, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        user_id: userId.toString(),
        content: sendContent
      })
    }).catch((error) =>
      console.log(error)
    );

    //  リダイレクト
    window.location.href = "/";
  }

  const GetPosts = async (url) => {
    try {
      let response = fetch(url);
      let data = await response.then((res) => res.json());
      setJson(data);
      console.log(data);
    } catch (error) {
      setJson(null);
    }
  };

  useEffect(() => {
    GetPosts(URL);
  }, []);

  return (
    <Router>
      <Routes>
        <Route path='/' element={(
          <div className="App">
            {userId && userId !== "" ?
              (
                <>
                  {/* ログインしていたら */}
                  < Logout setUserId={setUserId} />

                  {/* 投稿入力欄 */}
                  <div>
                    <InputField
                      setSendContent={setSendContent}
                      sendPost={SendPost}
                    />
                  </div>
                </>
              )
              :
              (
                <>
                  {/* // ログインしていなかったら */}
                  <h2>新規登録</h2>
                  < SignUp setUserId={setUserId} />
                  <h2>ログイン</h2>
                  < Login setUserId={setUserId} />
                </>
              )
            }
            {/* ボタン */}
            <button onClick={() => {
              GetPosts(URL);
            }}>最新データ取得</button>


            {/* 投稿一覧 */}
            {
              json && json.posts && json.posts.map((post) =>
                <Post
                  key={post.id}
                  content={post.content}
                  userId={post.user_id}
                  createTime={post.created_at}
                />
              )
            }
          </div>
        )
        } />
      </Routes>
    </Router>


  );
}

export default App;
