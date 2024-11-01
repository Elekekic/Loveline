import { Events } from "./events";

export interface User {
    id: number,
    username: string,
    name: string,
    surname: string,
    email: string,
    pfp: string,
    loverId: number,
    myLover: User,
    timeline: number,
    events: Events[],
    authorities: [
        {
            authority: String;
        }
    ];
    credentialsNonExpired: true;
    accountNonExpired: true;
    accountNonLocked: true;
}
