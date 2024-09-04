#pragma GCC optimize("O2")

#include <vector>
#include <iostream>

using namespace std;

typedef long long ll;

vector<ll> m_gcd(ll a, ll b) {
    if (a == 0) {
        return {b, 0, 1};
    }
    vector<ll> result = m_gcd(b % a, a);
    return {result[0], result[2] - result[1] * (b / a), result[1]};
}

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);

    int t;
    cin >> t;

    while (t--) {
        ll a, b, n, m;
        cin >> a >> b >> n >> m;

        ll d = m_gcd(n, m)[0];
        if ((a - b) % d != 0) {
            cout << "NO" << '\n';
            continue;
        }

        ll n1 = n / d;
        ll m1 = m / d;
        ll a1 = (b - a) / d;
        vector<ll> result = m_gcd(n1, m1);

        ll x1 = result[1];
        ll k = ((a1 * x1 % m1) + m1) % m1;

        ll x0 = (a + n * k) % (n * m / d);
        if (x0 < 0) x0 += n * m / d;

        cout << "YES " << x0 << " " << (n * m / d) << '\n';
    }

    return 0;
}