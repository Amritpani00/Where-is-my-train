export async function apiGet<T>(path: string): Promise<T> {
	const res = await fetch(path)
	if (!res.ok) {
		const text = await res.text()
		throw new Error(`GET ${path} failed: ${res.status} ${text}`)
	}
	return res.json() as Promise<T>
}

export async function apiPostJson<TReq, TRes>(path: string, body: TReq): Promise<TRes> {
	const res = await fetch(path, {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify(body),
	})
	if (!res.ok) {
		const text = await res.text()
		throw new Error(`POST ${path} failed: ${res.status} ${text}`)
	}
	return res.json() as Promise<TRes>
}

export async function apiPostForm<TRes>(path: string, form: URLSearchParams): Promise<TRes> {
	const res = await fetch(path, {
		method: 'POST',
		headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
		body: form.toString(),
	})
	if (!res.ok) {
		const text = await res.text()
		throw new Error(`POST ${path} failed: ${res.status} ${text}`)
	}
	return res.json() as Promise<TRes>
}