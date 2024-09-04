#pragma GCC optimize("O2")

#include <vector>
#include <iostream>
#include <algorithm>

using namespace std;

typedef long long ll;

struct point {
    ll x, y;

    bool operator==(const point& other) const {
        return x == other.x && y == other.y;
    }
};

struct segment {
    point a, b;

    static ll det(segment vec1, segment vec2) {
        return (vec1.b.x - vec1.a.x) * (vec2.b.y - vec2.a.y) -
               (vec1.b.y - vec1.a.y) * (vec2.b.x - vec2.a.x);
    }

    static ll scalar(segment vec1, segment vec2) {
        return (vec1.b.x - vec1.a.x) * (vec2.b.x - vec2.a.x) +
               (vec1.b.y - vec1.a.y) * (vec2.b.y - vec2.a.y);
    }
};

int orientation(point a, point b, point c) {
    ll val = (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
    if (val == 0) return 0;
    return (val > 0) ? 1 : -1;
}

bool check(int n, std::vector<point> &vec, point &a) {
    ll num_intersections = 0;
    for (int i = 0; i < n; i++) {
        point min_point = vec[i];
        point max_point = vec[(i + 1) % n];
        if (min_point.y > max_point.y)
            std::swap(min_point, max_point);
        if (max_point.y <= a.y || min_point.y > a.y) continue;
        int orient = orientation(min_point, max_point, a);
        if (orient == 0) return true;
        if (orient > 0) num_intersections++;
    }
    return num_intersections % 2;
}

bool cw(point a, point b, point c) {
    return a.x * (b.y - c.y) + b.x * (c.y - a.y) + c.x * (a.y - b.y) < 0;
}

bool ccw(point a, point b, point c) {
    return a.x * (b.y - c.y) + b.x * (c.y - a.y) + c.x * (a.y - b.y) > 0;

}

bool cmp(point a, point b) {
    return a.x < b.x || a.x == b.x && a.y < b.y;
}

vector<point> convex_hull(int n, vector<point>& a) {
    sort(a.begin(), a.end(), &cmp);
    point left = a[0]; point right = a.back();
    vector <point> top(0); vector <point> lower(0);

    top.push_back(left);
    lower.push_back(left);
    for (int i = 1; i < n; i++) {
        if (i == n - 1 || cw (left, a[i], right)) {
            while (top.size() >= 2 && !cw(top[top.size() - 2], top[top.size() - 1], a[i])) {
                top.pop_back();
            }
            top.push_back(a[i]);
        }
        if (i == n - 1 || ccw (left, a[i], right)) {
            while (lower.size() >= 2 && !ccw(lower[lower.size() - 2], lower[lower.size() - 1], a[i])) {
                lower.pop_back();
            }
            lower.push_back(a[i]);
        }
    }
    vector<point> ans(0);
    for (auto i : top) {
        ans.push_back(i);
    }
    for (int i = lower.size() - 2; i > 0; --i) {
        ans.push_back(lower[i]);
    }

    return ans;
}

bool on_segment(point &p, segment& seg) {
    return segment::det(seg, {seg.a, p}) == 0
        && segment::scalar({seg.a, p}, {seg.b, p}) <= 0;
}

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);

    int n;
    cin >> n;

    ll x, y;
    cin >> x >> y;
    point p = {x, y};

    vector<point> lions;
    for (int i = 0; i < n; i++) {
        cin >> x >> y;
        point lion = {x, y};
        if (lion == p) {
            cout << "NO" << endl;
            return 0;
        }
        lions.push_back(lion);
    }

    vector<point> hull = convex_hull(lions.size(), lions);

    for (int i = 0, size = hull.size(); i < size; i++) {
        segment seg = {hull[i], hull[(i + 1) % size]};
        if (on_segment(p, seg)) {
            cout << "YES" << endl;
            return 0;
        }
    }

    cout << (check(hull.size(), hull, p) ? "NO" : "YES") << endl;

    return 0;
}