import { useEffect, useState } from 'react'
import { apiGet } from '../api/client'

type BookingSummary = {
	id: number
	pnr: string
	trainNumber: string
	fromCode: string
	toCode: string
	travelDate: string
	status: string
	amount: string
	createdAt: string
}

export default function BookingsListPage() {
	const [items, setItems] = useState<BookingSummary[]>([])
	const [status, setStatus] = useState('')
	const [email, setEmail] = useState('')

	useEffect(() => {
		;(async () => {
			try {
				const json = await apiGet<BookingSummary[]>('/api/bookings?limit=50')
				setItems(json)
			} catch (e: any) {
				setStatus('Error: ' + e.message)
			}
		})()
	}, [])

	function download(b: BookingSummary) {
		window.open(`/api/payments/ticket/${b.id}`, '_blank')
	}

	async function emailTicket(b: BookingSummary) {
		if (!email) {
			setStatus('Enter email in the field above')
			return
		}
		try {
			const res = await fetch(`/api/email/ticket/${b.id}?to=${encodeURIComponent(email)}`, { method: 'POST' })
			if (!res.ok) throw new Error('Email failed')
			setStatus('Email sent')
		} catch (e: any) {
			setStatus('Email error: ' + e.message)
		}
	}

	return (
		<div style={{ padding: 16 }}>
			<h2>Recent Bookings</h2>
			<div style={{ marginBottom: 8 }}>
				<input placeholder="your@email" value={email} onChange={(e) => setEmail(e.target.value)} />
			</div>
			{status && <div>{status}</div>}
			<table style={{ borderCollapse: 'collapse', width: '100%' }}>
				<thead>
					<tr>
						<th align="left">ID</th>
						<th align="left">PNR</th>
						<th align="left">Train</th>
						<th align="left">From</th>
						<th align="left">To</th>
						<th align="left">Date</th>
						<th align="left">Amount</th>
						<th align="left">Actions</th>
					</tr>
				</thead>
				<tbody>
					{items.map((b) => (
						<tr key={b.id}>
							<td>{b.id}</td>
							<td>{b.pnr}</td>
							<td>{b.trainNumber}</td>
							<td>{b.fromCode}</td>
							<td>{b.toCode}</td>
							<td>{b.travelDate}</td>
							<td>{b.amount}</td>
							<td>
								<button onClick={() => download(b)}>PDF</button>
								<button onClick={() => emailTicket(b)} style={{ marginLeft: 8 }}>Email</button>
							</td>
						</tr>
					))}
				</tbody>
			</table>
		</div>
	)
}