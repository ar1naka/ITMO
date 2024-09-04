#include <iostream>
#include <vector>
#include <algorithm>
using namespace std;
struct Point {
    int x, y;
};

int orientation(Point a, Point b, Point c) {
    int val = (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
    if (val == 0) return 0;
    return (val > 0) ? 1 : -1;
}

bool check(int n, std::vector<Point> &vec, Point &a) {
    long long num_intersections = 0;
    for (int i = 0; i < n; i++) {
        Point min_point = vec[i];
        Point max_point = vec[(i + 1) % n];
        if (min_point.y >= max_point.y)
            std::swap(min_point, max_point);
        if (max_point.y < a.y || min_point.y > a.y) continue;
        int orient = orientation(min_point, max_point, a);
        if (orient == 0) return true;
        if (orient > 0) num_intersections++;
    }
    return num_intersections % 2;
}

int main() {
    int n;
    cin >> n;
    int x, y;
    cin >> x >> y;
    Point a = {x, y};

    std::vector<Point> vec(n);

    for (int i = 0; i < n; i++) {
        std::cin >> x >> y;
        vec[i] = {x, y};
    }

    bool status;
    status = check(n, vec, a);
    if (status) {
        cout << "YES" << endl;
    } else {
        cout << "NO" << endl;
    }

    return 0;
}