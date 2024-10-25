import './App.css';
import { useEffect, useState } from "react"

//  ルーティング
import { BrowserRouter as Router, Routes, Route } from "react-router-dom"
import Home from './components/Home';
import { GetPosts, SendPost } from './utils/RequestUtil';
import Auth from './components/Auth/Auth';

function App() {

  const GetPostUrl = "http://localhost:8080/post";
  const SendPostUrl = "http://localhost:8080/post";

  const [json, setJson] = useState(null)

  const [userId, setUserId] = useState(localStorage.getItem("userId"));
  const [sendContent, setSendContent] = useState("");

  const Send = async () => {
    const result = await SendPost(SendPostUrl, {
      user_id: userId.toString(),
      content: sendContent
    });

    console.log(result);

    //  リダイレクト
    window.location.href = "/";
  }

  const fetchPosts = async () => {
    const getJson = await GetPosts(GetPostUrl);
    if (getJson) {
      console.log(getJson);
      setJson(getJson);
    } else {
      console.log("データの取得に失敗");
    }
  }

  useEffect(() => {
    fetchPosts();
  }, []);

  //  自動更新用
  useEffect(() => {
    // 2秒ごとにカウントを1増やす
    const intervalId = setInterval(() => {
      fetchPosts();
    }, 2000);

    // クリーンアップ関数で interval をクリア
    return () => clearInterval(intervalId);
  }, []); // 空の依存配列で最初のレンダリング時に1度だけ実行


  return (
    <Router>
      <Routes>
        <Route path='/' element={
          <Home
            userId={userId}
            json={json}
            setSendContent={setSendContent}
            sendPost={Send}
          />}
        />
        <Route path='/auth' element={
          <Auth
            userId={userId}
            setUserId={setUserId}
          />}
        />
      </Routes>
    </Router>
  );
}

export default App;
