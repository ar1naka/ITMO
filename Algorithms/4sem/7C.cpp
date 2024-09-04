#pragma GCC optimize("Ofast")

#include <vector>
#include <iostream>

using namespace std;

typedef long long ll;

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);

    ll p;
    cin >> p;

    vector<int> r(p);
    r[1] = 1;

    for (int i = 2; i < p; ++i) {
        r[i] = (int) ((p - (ll) (p / i) * r[p % i] % p) % p);
    }

    ll sum = 0;
    for (int i = 1; i < p; ++i) {
        sum = (sum + r[i]) % p;

        if (i % 100 == 0 || i == p - 1) {
            cout << sum << '\n';
            sum = 0;
        }
    }

    return 0;
}