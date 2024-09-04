#include <iostream>
#include <vector>
#pragma GCC optimize("Ofast")


#define ll unsigned long long

using namespace std;

vector<int> primes = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37};

ll mul(ll a, ll b, ll mod) {
    ll result = 0;
    a = a % mod;
    while (b > 0) {
        if (b % 2 == 1) {
            result = (result + a) % mod;
        }
        a = (a * 2) % mod;
        b /= 2;
    }
    return result;
}

ll power(ll a, ll b, ll mod) {
    ll res = 1;
    a = a % mod;
    while (b > 0) {
        if (b % 2 == 1) {
            res = mul(res, a, mod);
        }
        a = mul(a, a, mod);
        b /= 2;
    }
    return res;
}

bool compose(ll n, ll a, ll d, int s) {
    ll x = power(a, d, n);
    if (x == 1 || x == n - 1) return false;
    for (int i = 1; i < s; ++i) {
        x = mul(x, x, n);
        if (x == n - 1) return false;
    }
    return true;
}

bool miller(ll n) {
    if (n < 2) return false;
    if (n == 2 || n == 3) return true;
    if (n % 2 == 0) return false;

    int s = 0;
    ll d = n - 1;
    while ((d & 1) == 0) {
        d >>= 1;
        s++;
    }

    for (int prime : primes) {
        if (n == prime) return true;
        if (compose(n, prime, d, s)) return false;
    }
    return true;
}

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);
    int n;
    cin >> n;

    for (int i = 0; i < n; i++) {
        ll x;
        cin >> x;

        bool is_prime = miller(x);

        if (is_prime) {
            cout << "YES" << endl;
        } else {
            cout << "NO" << endl;
        }
    }
    return 0;
}