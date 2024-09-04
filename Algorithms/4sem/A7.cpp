#include <iostream>
#include <tuple>
using namespace std;
long long gcd(long long a, long long b) {
    if (b == 0) {
        return a;
    }
    return gcd(b, a % b);
}

long long gcd(long long a, long long b, long long &x, long long &y) {
    if (b == 0) {
        x = 1;
        y = 0;
        return a;
    }
    long long g = gcd(b, a % b, y, x);
    y -= (a / b) * x;
    return g;
}

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);
    long long a, b, c;
    cin >> a >> b >> c;

    long long g, x0, y0;
    g = gcd(a, b, x0, y0);

    if (c % g != 0) {
        cout << "Impossible" << endl;
        return 0;
    } else {
        x0 *= c / g;
        y0 *= c / g;

        long long x, y;
        if (x0 < 0) {
            long long tmp = ((-x0 + b/g - 1) / (b/g));
            x = x0 + tmp * (b / g);
            y = y0 - tmp * (a / g);
        } else {
            long long tmp = (-(x0 / (b/g)));
            x = x0 + tmp * (b / g);
            y = y0 - tmp * (a / g);
        }
        cout << x << " " << y << endl;
    }

    return 0;
}