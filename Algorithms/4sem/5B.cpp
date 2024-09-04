#include <iostream>
#include <vector>
#pragma GCC optimize("Ofast")
#include <iomanip>
#include <cmath>

using namespace std;

struct Point {
    int x, y;
};
int cross(const Point& p1, const Point& p2) {
    return p1.x * p2.y - p1.y * p2.x;
}

int main() {
    Point a{}, o{}, b{}, p{};
    cin >> a.x >> a.y >> o.x >> o.y >> b.x >> b.y >> p.x >> p.y;

    Point oa = {a.x - o.x, a.y - o.y};
    Point ob = {b.x - o.x, b.y - o.y};
    Point op = {p.x - o.x, p.y - o.y};

    int sign1 = cross(oa, op);
    int sign2 = cross(oa, ob);
    int sign3 = cross(ob, op);
    int sign4 = cross(ob, oa);
    //cout << sign1 << " " << sign2;

    if (sign1 * sign2 >= 0 && sign3 * sign4 >= 0) {
        cout << "YES" << endl;
    } else {
        cout << "NO" << endl;
    }

    return 0;
}