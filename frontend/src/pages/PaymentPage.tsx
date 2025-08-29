import { useEffect, useState } from 'react'
import { apiGet } from '../api/client'

declare global {
	interface Window { Razorpay: any }
}

const RAZORPAY_KEY = import.meta.env.VITE_RAZORPAY_KEY_ID as string

type BookingSummary = {
	id: number
	pnr: string
	trainNumber: string
	fromCode: string
	toCode: string
	travelDate: string
	status: string
	amount: number | string
	createdAt: string
}

export default function PaymentPage() {
	const [bookingId, setBookingId] = useState<string>('')
	const [email, setEmail] = useState<string>('')
	const [status, setStatus] = useState<string>('')
	const [summary, setSummary] = useState<BookingSummary | null>(null)

	useEffect(() => {
		let ignore = false
		async function load() {
			setSummary(null)
			if (!bookingId) return
			try {
				const data = await apiGet<BookingSummary>(`/api/bookings/${bookingId}`)
				if (!ignore) setSummary(data)
			} catch (e: any) {
				setSummary(null)
				setStatus('Could not load booking: ' + e.message)
			}
		}
		load()
		return () => { ignore = true }
	}, [bookingId])

	async function createOrder(bid: string) {
		const res = await fetch(`/api/payments/create-order/${bid}`, { method: 'POST' })
		if (!res.ok) throw new Error('Failed to create order')
		return res.json()
	}

	function openCheckout(order: any) {
		const options = {
			key: RAZORPAY_KEY,
			amount: order.amount,
			currency: order.currency,
			name: 'Ticket Booking',
			description: summary ? `PNR ${summary.pnr}` : 'Train ticket',
			order_id: order.orderId,
			prefill: { email },
			handler: async function (response: any) {
				try {
					const params = new URLSearchParams({
						razorpay_order_id: response.razorpay_order_id,
						razorpay_payment_id: response.razorpay_payment_id,
						razorpay_signature: response.razorpay_signature,
						bookingId,
						email,
					})
					const verifyRes = await fetch(`/api/payments/verify`, {
						method: 'POST',
						headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
						body: params.toString(),
					})
					if (!verifyRes.ok) throw new Error('Verification failed')
					setStatus('Payment successful. Ticket emailed.')
				} catch (e: any) {
					setStatus('Error after payment: ' + e.message)
				}
			},
			theme: { color: '#3399cc' },
		}
		const rzp = new window.Razorpay(options)
		rzp.open()
	}

	async function onPay() {
		setStatus('')
		try {
			if (!bookingId) throw new Error('Enter bookingId')
			if (!email) throw new Error('Enter email')
			const order = await createOrder(bookingId)
			openCheckout(order)
		} catch (e: any) {
			setStatus('Error: ' + e.message)
		}
	}

	return (
		<div style={{ padding: 16 }}>
			<h2>Payment</h2>
			<div style={{ display: 'flex', flexDirection: 'column', gap: 8, maxWidth: 480 }}>
				<input placeholder="Booking ID" value={bookingId} onChange={(e) => setBookingId(e.target.value)} />
				{summary && (
					<div style={{ background: '#f8f8f8', padding: 8, borderRadius: 4 }}>
						<div>PNR: {summary.pnr}</div>
						<div>Route: {summary.fromCode} → {summary.toCode} ({summary.trainNumber})</div>
						<div>Date: {summary.travelDate}</div>
						<div>Amount: ₹{summary.amount}</div>
					</div>
				)}
				<input placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} />
				<button onClick={onPay} disabled={!bookingId}>Pay with Razorpay</button>
				{status && <div>{status}</div>}
			</div>
		</div>
	)
}