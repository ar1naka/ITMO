#pragma GCC optimize("O3")

#include <vector>
#include <climits>
#include <iostream>

using namespace std;

struct edge {
    int a;
    int b;
    int w;
};

vector<edge> g;

bool ford_bellman(int n) {
    bool has_cycle;
    vector<long long> d(n);
    for (size_t i = 0; i < n; i++) {
        has_cycle = false;
        for (auto & e : g) {
            if (d[e.a] + e.w < d[e.b]) {
                d[e.b] = max(d[e.a] + e.w, LONG_LONG_MIN);
                has_cycle = true;
            }
        }
    }
    return has_cycle;
}

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);

    int n;
    cin >> n;

    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            int w;
            cin >> w;
            if (w < 100000) {
                g.push_back({i, j, w});
            }
        }
    }

    cout << (ford_bellman(n) ? "YES" : "NO") << endl;

    return 0;
}