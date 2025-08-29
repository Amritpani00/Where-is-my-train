import { useState } from 'react'

export default function BookingPage() {
	const [trainNumber, setTrainNumber] = useState('12345')
	const [trainName, setTrainName] = useState('Test Express')
	const [fromCode, setFromCode] = useState('SRC')
	const [toCode, setToCode] = useState('DST')
	const [travelDate, setTravelDate] = useState('2025-12-31')
	const [amount, setAmount] = useState('1000.00')
	const [result, setResult] = useState<any>(null)
	const [status, setStatus] = useState('')

	async function onCreate() {
		setStatus('')
		setResult(null)
		try {
			const res = await fetch('/api/bookings', {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify({ trainNumber, trainName, fromCode, toCode, travelDate, amount }),
			})
			if (!res.ok) throw new Error('Failed to create booking')
			const json = await res.json()
			setResult(json)
		} catch (e: any) {
			setStatus('Error: ' + e.message)
		}
	}

	return (
		<div style={{ padding: 16 }}>
			<h2>Create Test Booking</h2>
			<div style={{ display: 'flex', flexDirection: 'column', gap: 8, maxWidth: 420 }}>
				<input placeholder="Train Number" value={trainNumber} onChange={(e) => setTrainNumber(e.target.value)} />
				<input placeholder="Train Name" value={trainName} onChange={(e) => setTrainName(e.target.value)} />
				<input placeholder="From Code" value={fromCode} onChange={(e) => setFromCode(e.target.value)} />
				<input placeholder="To Code" value={toCode} onChange={(e) => setToCode(e.target.value)} />
				<input placeholder="Travel Date (YYYY-MM-DD)" value={travelDate} onChange={(e) => setTravelDate(e.target.value)} />
				<input placeholder="Amount" value={amount} onChange={(e) => setAmount(e.target.value)} />
				<button onClick={onCreate}>Create Booking</button>
				{status && <div>{status}</div>}
				{result && (
					<div>
						<p>Booking ID: {result.bookingId}</p>
						<p>PNR: {result.pnr}</p>
						<p>Amount: {result.amount}</p>
						<p>
							Go to payment: <a href={`/pay`}>/pay</a> (enter Booking ID and Email)
						</p>
					</div>
				)}
			</div>
		</div>
	)
}