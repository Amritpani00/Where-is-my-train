import { BrowserRouter, Routes, Route, Link } from 'react-router-dom'
import PaymentPage from './pages/PaymentPage'
import BookingPage from './pages/BookingPage'
import BookingsListPage from './pages/BookingsListPage'
import { useEffect, useState } from 'react'

function App() {
	const [toasts, setToasts] = useState<string[]>([])
	useEffect(() => {
		const ev = new EventSource('/api/events/stream')
		ev.addEventListener('booking.created', (e: any) => {
			setToasts((t) => [`Booking created: ${(e as MessageEvent).data}`, ...t].slice(0, 5))
		})
		ev.addEventListener('payment.success', (e: any) => {
			setToasts((t) => [`Payment success: ${(e as MessageEvent).data}`, ...t].slice(0, 5))
		})
		ev.onerror = () => {}
		return () => ev.close()
	}, [])

	return (
		<BrowserRouter>
			<nav style={{ padding: 12, borderBottom: '1px solid #eee' }}>
				<Link to="/">Home</Link>
				<span style={{ margin: '0 8px' }}>|</span>
				<Link to="/book">Create Booking</Link>
				<span style={{ margin: '0 8px' }}>|</span>
				<Link to="/pay">Pay</Link>
				<span style={{ margin: '0 8px' }}>|</span>
				<Link to="/bookings">Bookings</Link>
			</nav>
			<div style={{ position: 'fixed', right: 16, top: 16, display: 'flex', flexDirection: 'column', gap: 8 }}>
				{toasts.map((m, i) => (
					<div key={i} style={{ background: '#333', color: 'white', padding: 8, borderRadius: 4, maxWidth: 360 }}>{m}</div>
				))}
			</div>
			<Routes>
				<Route path="/" element={<div style={{ padding: 16 }}>Welcome</div>} />
				<Route path="/pay" element={<PaymentPage />} />
				<Route path="/book" element={<BookingPage />} />
				<Route path="/bookings" element={<BookingsListPage />} />
			</Routes>
		</BrowserRouter>
	)
}

export default App
