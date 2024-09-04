#pragma GCC optimize("Ofast")

#include <iostream>
#include <vector>
#include <algorithm>
#include <cmath>

using namespace std;
struct point {
    long long x;
    long long y;
    point (long long  x = 0, long long  y = 0): x(x), y(y) {}
    bool operator< (const point &p) const {
        return x < p.x || (x == p.x && y < p.y);
    }

    bool operator==(const point& other) const {
        return x == other.x && y == other.y;
    }
};

bool cw (point a, point b, point c) {
    return a.x * (b.y - c.y) + b.x * (c.y - a.y) + c.x * (a.y - b.y) < 0;
}

bool ccw (point a, point b, point c) {
    return a.x * (b.y - c.y) + b.x * (c.y - a.y) + c.x * (a.y - b.y) > 0;

}

bool cmp (point a, point b) {
    return a.x < b.x || a.x == b.x && a.y < b.y;
}

int main() {
    int n;
    cin >> n;
    vector<point> a(0);

    for (int i = 0; i < n; i++) {
        long long x, y;
        cin >> x >> y;
        a.emplace_back(x,y);
    }
    sort(a.begin(), a.end(), cmp);
    point left = a[0]; point right = a.back();
    vector <point> top(0); vector <point> lower(0);

    top.push_back(left);
    lower.push_back(left);
    for (int i = 1; i < n; i++) {
        if (i == n - 1 || cw (left, a[i], right)) {
            while (top.size() >= 2 && !cw (top[top.size() - 2], top[top.size() - 1], a[i])) {
                top.pop_back();
            }
            top.push_back (a[i]);
        }
        if (i == n - 1 || ccw (left, a[i], right)) {
            while (lower.size() >= 2 && !ccw(lower[lower.size() - 2], lower[lower.size() - 1], a[i])) {
                lower.pop_back();
            }
            lower.push_back (a[i]);
        }
    }
    vector<point> ans(0);
    for (auto i : top) {
        ans.push_back(i);
    }
    for (int i = lower.size() - 2; i > 0; --i) {
        ans.push_back(lower[i]);
    }

    cout << ans.size() << endl;
    long long area = 0;
    for (size_t i = 0; i < ans.size(); i++) {
        size_t next = (i + 1) % ans.size();
        area += (ans[next].x - ans[i].x) * (ans[next].y + ans[i].y);
    }

    for (auto p : ans) {
        cout << p.x << ' ' << p.y << '\n';
    }

    if (!(area & 1)) {
        cout << fixed << abs(area) / 2 << ".0" << endl;
    } else {
        cout << fixed << abs(area) / 2 << ".5" << endl;
    }

}