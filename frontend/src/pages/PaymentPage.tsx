import { useState } from 'react'

declare global {
	interface Window { Razorpay: any }
}

const RAZORPAY_KEY = import.meta.env.VITE_RAZORPAY_KEY_ID as string

export default function PaymentPage() {
	const [bookingId, setBookingId] = useState<string>('')
	const [email, setEmail] = useState<string>('')
	const [status, setStatus] = useState<string>('')

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
			description: 'Train ticket',
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
			<div style={{ display: 'flex', flexDirection: 'column', gap: 8, maxWidth: 360 }}>
				<input placeholder="Booking ID" value={bookingId} onChange={(e) => setBookingId(e.target.value)} />
				<input placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} />
				<button onClick={onPay}>Pay with Razorpay</button>
				{status && <div>{status}</div>}
			</div>
		</div>
	)
}