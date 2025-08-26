import { BrowserRouter, Routes, Route, Link } from 'react-router-dom'
import PaymentPage from './pages/PaymentPage'

function App() {
	return (
		<BrowserRouter>
			<nav style={{ padding: 12, borderBottom: '1px solid #eee' }}>
				<Link to="/">Home</Link>
				<span style={{ margin: '0 8px' }}>|</span>
				<Link to="/pay">Pay</Link>
			</nav>
			<Routes>
				<Route path="/" element={<div style={{ padding: 16 }}>Welcome</div>} />
				<Route path="/pay" element={<PaymentPage />} />
			</Routes>
		</BrowserRouter>
	)
}

export default App
