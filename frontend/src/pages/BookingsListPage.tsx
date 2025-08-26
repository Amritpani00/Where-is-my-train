import { useEffect, useState } from 'react'

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

	useEffect(() => {
		;(async () => {
			try {
				const res = await fetch('/api/bookings?limit=50')
				if (!res.ok) throw new Error('Failed to load bookings')
				const json = await res.json()
				setItems(json)
			} catch (e: any) {
				setStatus('Error: ' + e.message)
			}
		})()
	}, [])

	return (
		<div style={{ padding: 16 }}>
			<h2>Recent Bookings</h2>
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
						<th align="left">Pay</th>
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
								<a href="/pay">Pay</a>
							</td>
						</tr>
					))}
				</tbody>
			</table>
		</div>
	)
}